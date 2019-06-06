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
                    SendPushNotification(token);
                    tokenCache.Set(token, DateTime.Now, DateTimeOffset.UtcNow.AddHours(intervalBetweenNotifications));
                    return true;
                }
            }
            return false;
        }

        private async Task<bool> SendPushNotification(string deviceId)
        {

            using (HttpClient client = new HttpClient())
            {
                //do something with http client
                client.BaseAddress = new Uri(firebaseEndpoint);
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
                client.DefaultRequestHeaders.TryAddWithoutValidation("Authorization", $"key={applicationID}");
                client.DefaultRequestHeaders.TryAddWithoutValidation("Sender", $"id={senderID}");


                object data = new
                {
                    to = deviceId,
                    notification = new
                    {
                        body = "Landmarks",
                        title = "Estas cerca de un landmark",
                        icon = "myicon"
                    },
                    priority = "high"

                };

                string json = JsonConvert.SerializeObject(data);
                StringContent httpContent = new StringContent(json, Encoding.UTF8, "application/json");

                HttpResponseMessage result = await client.PostAsync("/fcm/send", httpContent);
            }
            return true;
        }
    }
}
