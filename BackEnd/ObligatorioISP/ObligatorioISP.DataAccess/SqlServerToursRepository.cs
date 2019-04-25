

using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;

namespace ObligatorioISP.DataAccess
{
    public class SqlServerToursRepository: IToursRepository
    {
        private string connectionString;
        private ILandmarksRepository landmarks;

        public SqlServerToursRepository(string aConnectionString, ILandmarksRepository aRepository) {
            connectionString = aConnectionString;
            landmarks = aRepository;
        }

        public TourDto GetById(int id)
        {
            string command = $"SELECT * FROM Tour WHERE ID = {id};";

            using (SqlConnection client = new SqlConnection(connectionString))
            {
                client.Open();
                using (SqlCommand sqlCmd = new SqlCommand(command, client))
                {
                    using (SqlDataReader reader = sqlCmd.ExecuteReader())
                    {

                        reader.Read();
                        TourDto retrieved = BuildTour(reader);
                        return retrieved;                     
                    }
                }
            }
        }

        public ICollection<TourDto> GetToursWithinKmRange(double centerLat, double centerLng, double rangeInKm)
        {
            string command = $"SELECT T.* FROM Tour T " 
                + $"WHERE NOT EXISTS (" 
                + $"SELECT 1 FROM Landmark L, LandmarkTour LT" 
                + $" WHERE L.ID = LT.LANDMARK_ID AND T.ID = LT.TOUR_ID " 
                + $"AND dbo.DISTANCE({centerLat},{centerLng},L.LATITUDE, L.LONGITUDE) > {rangeInKm});";

            ICollection<TourDto> result = new List<TourDto>();

            using (SqlConnection client = new SqlConnection(connectionString))
            {
                client.Open();
                using (SqlCommand sqlCmd = new SqlCommand(command, client))
                {
                    using (SqlDataReader reader = sqlCmd.ExecuteReader())
                    {

                        while (reader.Read())
                        {
                            TourDto retrieved = BuildTour(reader);
                            result.Add(retrieved);
                        }
                    }
                }
            }
            return result;
        }

        private TourDto BuildTour(SqlDataReader reader)
        {
            int tourId = Int32.Parse(reader["ID"].ToString());
            string title = reader["TITLE"].ToString();
            ICollection<LandmarkDto> tourStops = landmarks.GetTourLandmarks(tourId);
            TourDto tour = new TourDto() {Id=tourId };
            return tour;
        }
    }
}
