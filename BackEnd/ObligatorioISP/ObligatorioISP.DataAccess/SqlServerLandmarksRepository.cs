

using System.Collections.Generic;
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
            throw new System.NotImplementedException();
        }
    }
}
