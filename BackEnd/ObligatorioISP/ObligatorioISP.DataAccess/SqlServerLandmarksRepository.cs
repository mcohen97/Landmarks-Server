

using System.Collections.Generic;
using System.Data.SqlClient;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;

namespace ObligatorioISP.DataAccess
{
    public class SqlServerLandmarksRepository: ILandmarksRepository
    {
        private string connectionString;
        public SqlServerLandmarksRepository(string connString) {
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
                            result.Add(new LandmarkDto());
                        }
                    }
                }
            }

            return result;
        }
    }
}
