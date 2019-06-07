using Microsoft.Extensions.Caching.Memory;
using Microsoft.Extensions.Configuration;
using Newtonsoft.Json;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.Services.Contracts;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

namespace ObligatorioISP.Services
{
    public class FirebaseNotificationService : IProximityNotificationService
    {
        private ILandmarksRepository landmarks;
        private IMemoryCache tokenCache;
        private string applicationID;
        private string senderID;
        private double proximityDistance;
        private double intervalBetweenNotifications;
        private string firebaseEndpoint;

        public FirebaseNotificationService(IConfiguration configuration, ILandmarksRepository landmarksStorage, IMemoryCache cache)
        {
            landmarks = landmarksStorage;
            tokenCache = cache;
            firebaseEndpoint = configuration["Firebase:Url"];
            applicationID = configuration["Firebase:ApplicationID"];
            senderID = configuration["Firebase:SenderID"];
            proximityDistance = Double.Parse(configuration["ProximityNotifications:MaxDistance"]);
            //We use this interval to avoid bombarding the user with notifications, since they are not critical.
            intervalBetweenNotifications = Double.Parse(configuration["ProximityNotifications:NotifMinimumIntervalHours"]);
        }

        public async Task<bool> NotifyIfCloseToLandmark(string token, double lat, double lng)
        {
            DateTime time;
            if (!tokenCache.TryGetValue(token, out time))
            {
                //500 m distance, get first landmark.
                ICollection<Landmark> closeLandmarks = landmarks.GetWithinZone(lat, lng, proximityDistance, 0, 1);
                if (closeLandmarks.Any())
                {
                    Landmark toSend = closeLandmarks.First();
                    SendPushNotification(token,toSend, ComputeDistanceMeters(lat,lng,toSend.Latitude, toSend.Longitude));
                    tokenCache.Set(token, DateTime.Now, DateTimeOffset.UtcNow.AddHours(intervalBetweenNotifications));
                    return true;
                }
            }
            return false;
        }

        private async Task<bool> SendPushNotification(string deviceId, Landmark landmark, double distance)
        {

            using (HttpClient client = new HttpClient())
            {
                //do something with http client
                client.BaseAddress = new Uri(firebaseEndpoint);
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
                client.DefaultRequestHeaders.TryAddWithoutValidation("Authorization", $"key={applicationID}");
                client.DefaultRequestHeaders.TryAddWithoutValidation("Sender", $"id={senderID}");

                string description = $"No te pierdas de visitar el landmark: {landmark.Title} se encuentra a {distance} m";


                object data = new
                {
                    to = deviceId,
                    data = new {
                        title = "Estas cerca de un landmark",
                        body = description,
                        landmarkId = landmark.Id,
                    },
                    priority = "high"

                };

                string json = JsonConvert.SerializeObject(data);
                StringContent httpContent = new StringContent(json, Encoding.UTF8, "application/json");

                HttpResponseMessage result = await client.PostAsync("/fcm/send", httpContent);
            }
            return true;
        }

        private double ComputeDistanceMeters(double lat1, double lon1, double lat2, double lon2)
        {
            int earthRadiusM = 6371 * 1000;

            double dLat = degreesToRadians(lat2 - lat1);
            double dLon = degreesToRadians(lon2 - lon1);

            lat1 = degreesToRadians(lat1);
            lat2 = degreesToRadians(lat2);

            double a = Math.Sin(dLat / 2) * Math.Sin(dLat / 2) +
                    Math.Sin(dLon / 2) * Math.Sin(dLon / 2) * Math.Cos(lat1) * Math.Cos(lat2);
            double c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            return earthRadiusM * c;
        }

        private double degreesToRadians(double degrees)
        {
            return degrees * Math.PI / 180;
        }

    }
}
