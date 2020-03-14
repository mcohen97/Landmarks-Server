using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
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

        public ICollection<Landmark> GetWithinZone(double centerLat, double centerLng, double distanceInKm, int offset = 0, int count = 50)
        {
            string centerLatStr = centerLat.ToString(CultureInfo.InvariantCulture);
            string centerLngStr = centerLng.ToString(CultureInfo.InvariantCulture);
            string distanceInKmStr = distanceInKm.ToString(CultureInfo.InvariantCulture);
            //Could not find a way to reuse the result of distance and not calculate it twice, should be improved.
            string command = $"SELECT * "
                + $"FROM Landmark "
                + $"WHERE dbo.DISTANCE({centerLatStr},{centerLngStr}, LATITUDE, LONGITUDE) <= {distanceInKmStr} "
                + $"ORDER BY dbo.DISTANCE({centerLatStr},{centerLngStr}, LATITUDE, LONGITUDE) ASC "
                + $"OFFSET {offset} ROWS FETCH NEXT {count} ROWS ONLY;";

            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            ICollection<Landmark> result = rows.Select(r => BuildLandmark(r)).ToList();
            return result;
        }

        public ICollection<Landmark> GetTourLandmarks(int tourId)
        {
            string command = $"SELECT 1 FROM Tour WHERE ID = {tourId};";

            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);

            if (!rows.Any())
            {
                throw new TourNotFoundException();
            }

            command = $"SELECT L.* FROM Landmark L, LandmarkTour LT"
                + $" WHERE LT.TOUR_ID = {tourId} AND LT.LANDMARK_ID = L.ID;";

            rows = connection.ExcecuteRead(command);
            ICollection<Landmark> result = rows.Select(r => BuildLandmark(r)).ToList();
            return result;
        }

        public Landmark GetById(int id)
        {
            string command = $"SELECT * "
                + $"FROM Landmark "
                + $"WHERE ID = {id};";
            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);

            if (!rows.Any())
            {
                throw new LandmarkNotFoundException();
            }
            Landmark selected = BuildLandmark(rows.First());
            return selected;
        }

        private Landmark BuildLandmark(Dictionary<string, object> rawData)
        {

            int id = Int32.Parse(rawData["ID"].ToString());
            string title = rawData["TITLE"].ToString();
            string description = rawData["DESCRIPTION"].ToString();
            double lat = double.Parse(rawData["LATITUDE"].ToString());
            double lng = double.Parse(rawData["LONGITUDE"].ToString());
            ICollection<string> images = GetMediaResources(id, IMAGES_TABLE);
            ICollection<string> audios = GetMediaResources(id, AUDIOS_TABLE);

            Landmark landmark;
            try
            {
                landmark = new Landmark(id, title, lat, lng, description, images, audios);
            }
            catch (InvalidLandmarkException e)
            {
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

        private string BuildPath(Dictionary<string, object> rawData, int landmarkId, string table)
        {
            string resourceId = rawData["ID"].ToString();
            string path;
            char separator = Path.DirectorySeparatorChar;
            if (table.Equals(IMAGES_TABLE))
            {
                string extension = rawData["EXTENSION"].ToString();
                path = $"{imagesDirectory}{separator}{landmarkId}{SEPARATOR}{resourceId}.{extension}";
            }
            else
            {
                path = $"{audiosDirectory}{separator}{landmarkId}{SEPARATOR}{resourceId}.mp3";
            }
            return path;
        }
    }
}
