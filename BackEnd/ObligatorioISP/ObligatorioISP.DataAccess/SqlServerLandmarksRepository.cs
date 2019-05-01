using System;
using System.Collections.Generic;
using System.Linq;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.BusinessLogic.Exceptions;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Exceptions;

namespace ObligatorioISP.DataAccess
{
    public class SqlServerLandmarksRepository : ILandmarksRepository
    {
        private static string IMAGES_TABLE = "LandmarkImages";
        private static string AUDIOS_TABLE = "LandmarkAudios";
        private static string SEPARATOR = "_";

        private string imagesDirectory;
        private string audiosDirectory;

        private ISqlContext connection;
        public SqlServerLandmarksRepository(ISqlContext context, string imagesPath, string audiosPath)
        {
            connection = context;
            imagesDirectory = imagesPath;
            audiosDirectory = audiosPath;
        }

        public ICollection<Landmark> GetWithinZone(double centerLat, double centerLng, double distanceInKm)
        {
            string command = $"SELECT * "
                + $"FROM Landmark "
                + $"WHERE dbo.DISTANCE({centerLat},{centerLng}, LATITUDE, LONGITUDE) <= {distanceInKm};";

            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            ICollection<Landmark> result = rows.Select(r => BuildLandmark(r)).ToList();
            return result;
        }

        public ICollection<Landmark> GetTourLandmarks(int tourId)
        {
            string command = $"SELECT L.* FROM Landmark L, LandmarkTour LT"
                + $" WHERE LT.TOUR_ID = {tourId} AND LT.LANDMARK_ID = L.ID;";

            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            ICollection<Landmark> result = rows.Select(r => BuildLandmark(r)).ToList();
            return result;
        }

        public Landmark GetById(int id)
        {
            string command = $"SELECT * "
                + $"FROM Landmark "
                + $"WHERE ID = {id};";
            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);

            if (!rows.Any()) {
                throw new LandmarkNotFoundException();
            }
            Landmark selected = BuildLandmark(rows.First());
            return selected;
        }

        private Landmark BuildLandmark(Dictionary<string,object> rawData)
        {

            int id = Int32.Parse(rawData["ID"].ToString());
            string title = rawData["TITLE"].ToString();
            string description = rawData["DESCRIPT"].ToString();
            double lat = double.Parse(rawData["LATITUDE"].ToString());
            double lng = double.Parse(rawData["LONGITUDE"].ToString());
            ICollection<string> images = GetMediaResources(id, IMAGES_TABLE);
            ICollection<string> audios = GetMediaResources(id, AUDIOS_TABLE);

            Landmark landmark;
            try
            {
                landmark = new Landmark(id, title, lat, lng, description, images, audios);
            }
            catch (InvalidLandmarkException) {
                throw new CorruptedDataException();
            }
            return landmark;
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
