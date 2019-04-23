

using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;

namespace ObligatorioISP.DataAccess
{
    public class SqlServerLandmarksRepository: ILandmarksRepository
    {
        private static string IMAGES_TABLE = "LandmarkImages";
        private static string AUDIOS_TABLE = "LandmarkAudios";
        private static string SEPARATOR = "_";

        private string connectionString;
        private string imagesDirectory = "Images";
        private string audiosDirectory = "Audios";
        public SqlServerLandmarksRepository(string connString, string imagesPath,string audiosPath) {
            connectionString = connString;
        }

        public ICollection<LandmarkDto> GetWithinCoordenates(double leftBottomLat, double leftBottomLng, double topRightLat, double topRightLng)
        {
            string command = $"SELECT * FROM Landmark " 
                +$"WHERE ((LATITUDE>= {topRightLat} AND LATITUDE <= {leftBottomLat}) OR (LATITUDE>= {leftBottomLat} AND LATITUDE <= {topRightLat}))"
                +$"AND ((LONGITUDE >= {topRightLng} AND LONGITUDE <= {leftBottomLng}) OR (LONGITUDE >= {leftBottomLng} AND LONGITUDE <= {topRightLng}));";

            ICollection<LandmarkDto> result = new List<LandmarkDto>();

            using (SqlConnection client = new SqlConnection(connectionString))
            {
                client.Open();
                using (SqlCommand sqlCmd = new SqlCommand(command, client))
                {
                    using (SqlDataReader reader =sqlCmd.ExecuteReader()) {
                        
                        while (reader.Read()) {
                            LandmarkDto dto = BuildLandmark(reader);
                            result.Add(dto);
                        }
                    }
                }
            }

            return result;
        }

        public ICollection<LandmarkDto> GetTourLandmarks(int tourId)
        {
            string command = $"SELECT L.* FROM Landmark L, LandmarkTour LT" 
                + $" WHERE LT.TOUR_ID = {tourId} AND LT.LANDMARK_ID = L.ID;";

            ICollection<LandmarkDto> result = new List<LandmarkDto>();

            using (SqlConnection client = new SqlConnection(connectionString))
            {
                client.Open();
                using (SqlCommand sqlCmd = new SqlCommand(command, client))
                {
                    using (SqlDataReader reader = sqlCmd.ExecuteReader())
                    {

                        while (reader.Read())
                        {
                            LandmarkDto dto = BuildLandmark(reader);
                            result.Add(dto);
                        }
                    }
                }
            }

            return result;
        }

        private LandmarkDto BuildLandmark(SqlDataReader reader)
        {
            int id = Int32.Parse(reader["ID"].ToString());
            string title = reader["TITLE"].ToString();
            string description = reader["DESCRIPT"].ToString();
            double lat = double.Parse(reader["LATITUDE"].ToString());
            double lng = double.Parse(reader["LONGITUDE"].ToString());
            ICollection<string> images = GetMediaResources(id, IMAGES_TABLE);
            ICollection<string> audios = GetMediaResources(id, AUDIOS_TABLE);

            //important to instantiate Landmark first, because it checks that database data is consistent.
            Landmark landmark = new Landmark(id, title, lat, lng,description ,images, audios);

            return ConvertToDto(landmark);
        }

        private ICollection<string> GetMediaResources(int landmarkId, string table)
        {
            string command = $"SELECT * FROM {table} WHERE LANDMARK_ID = {landmarkId} ORDER BY ID ASC";

            ICollection<string> result = new List<string>();

            using (SqlConnection client = new SqlConnection(connectionString))
            {
                client.Open();
                using (SqlCommand sqlCmd = new SqlCommand(command, client))
                {
                    using (SqlDataReader reader = sqlCmd.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            string resourceId = reader["ID"].ToString();
                            string path;
                            if (table.Equals(IMAGES_TABLE))
                            {
                                string extension = reader["EXTENSION"].ToString();
                                path = $"{imagesDirectory}/{landmarkId}{SEPARATOR}{resourceId}.{extension}";
                            }
                            else {
                                path = $"{audiosDirectory}/{landmarkId}{SEPARATOR}{resourceId}.mp3";
                            }
                            result.Add(path);
                        }
                    }
                }
            }
            return result;
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
    }
}
