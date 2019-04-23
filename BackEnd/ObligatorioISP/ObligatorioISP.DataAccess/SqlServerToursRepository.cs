

using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;

namespace ObligatorioISP.DataAccess
{
    public class SqlServerToursRepository
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
