

namespace ObligatorioISP.DataAccess
{
    public class SqlServerLandmarksRepository
    {
        private string connectionString;
        public SqlServerLandmarksRepository(string connString) {
            connectionString = connString;
        }
    }
}
