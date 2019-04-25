using System;
using System.Collections.Generic;
using System.Linq;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;

namespace ObligatorioISP.DataAccess
{
    public class SqlServerLandmarksRepository : ILandmarksRepository
    {
        private static string IMAGES_TABLE = "LandmarkImages";
        private static string AUDIOS_TABLE = "LandmarkAudios";
        private static string SEPARATOR = "_";

        private string connectionString;
        private string imagesDirectory = "Images";
        private string audiosDirectory = "Audios";

        private SqlServerConnectionManager connection;
        public SqlServerLandmarksRepository(string connString, string imagesPath, string audiosPath)
        {
            connectionString = connString;
            connection = new SqlServerConnectionManager(connectionString);
        }

        public ICollection<LandmarkDto> GetWithinZone(double centerLat, double centerLng, double distanceInKm)
        {
            string command = $"SELECT * "
                + $"FROM Landmark "
                + $"WHERE dbo.DISTANCE({centerLat},{centerLng}, LATITUDE, LONGITUDE) <= {distanceInKm};";

            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            ICollection<LandmarkDto> result = rows.Select(r => BuildLandmark(r)).ToList();
            return result;
        }

        public ICollection<LandmarkDto> GetTourLandmarks(int tourId)
        {
            string command = $"SELECT L.* FROM Landmark L, LandmarkTour LT"
                + $" WHERE LT.TOUR_ID = {tourId} AND LT.LANDMARK_ID = L.ID;";

            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            ICollection<LandmarkDto> result = rows.Select(r => BuildLandmark(r)).ToList();
            return result;
        }

        private LandmarkDto BuildLandmark(Dictionary<string,object> rawData)
        {

            int id = Int32.Parse(rawData["ID"].ToString());
            string title = rawData["TITLE"].ToString();
            string description = rawData["DESCRIPT"].ToString();
            double lat = double.Parse(rawData["LATITUDE"].ToString());
            double lng = double.Parse(rawData["LONGITUDE"].ToString());
            ICollection<string> images = GetMediaResources(id, IMAGES_TABLE);
            ICollection<string> audios = GetMediaResources(id, AUDIOS_TABLE);

            //important to instantiate Landmark first, because it checks that database data is consistent.
            Landmark landmark = new Landmark(id, title, lat, lng, description, images, audios);

            return ConvertToDto(landmark);
        }

        private LandmarkDto ConvertToDto(Landmark landmark)
        {
            return new LandmarkDto()
            {
                Id = landmark.Id,
                Title = landmark.Title,
                Latitude = landmark.Latitude,
                Longitude = landmark.Longitude,
                Description = landmark.Description,
                ImageBase64 = landmark.Images.First()
            };
        }

        private ICollection<string> GetMediaResources(int landmarkId, string table)
        {
            string command = $"SELECT * FROM {table} WHERE LANDMARK_ID = {landmarkId} ORDER BY ID ASC";

            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            ICollection<string> result = rows.Select(r => BuildPath(r, landmarkId, table)).ToList();
            return result;
        }

        private string BuildPath(Dictionary<string, object> rawData, int landmarkId, string table) {
            string resourceId = rawData["ID"].ToString();
            string path;
            if (table.Equals(IMAGES_TABLE))
            {
                string extension = rawData["EXTENSION"].ToString();
                path = $"{imagesDirectory}/{landmarkId}{SEPARATOR}{resourceId}.{extension}";
            }
            else
            {
                path = $"{audiosDirectory}/{landmarkId}{SEPARATOR}{resourceId}.mp3";
            }
            return path;
        }
    }
}
