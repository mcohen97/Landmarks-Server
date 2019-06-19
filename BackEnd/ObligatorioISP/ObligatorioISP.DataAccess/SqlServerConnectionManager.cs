using ObligatorioISP.DataAccess.Contracts.Exceptions;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;

namespace ObligatorioISP.DataAccess
{
    public class SqlServerConnectionManager : ISqlContext
    {
        private string connectionString;
        public SqlServerConnectionManager(string aConnString)
        {
            connectionString = aConnString;
        }

        public void ExcecuteCommand(string command)
        {
            try
            {
                TryExcecuteCommand(command);
            }
            catch (SqlException)
            {
                throw new DataInaccessibleException();
            }
        }

        private void TryExcecuteCommand(string command)
        {
            using (SqlConnection connection = new SqlConnection(connectionString))
            {
                connection.Open();
                using (SqlCommand sqlCmd = new SqlCommand(command, connection))
                {
                    sqlCmd.ExecuteNonQuery();
                }
            }
        }

        public ICollection<Dictionary<string, object>> ExcecuteRead(string query)
        {
            ICollection<Dictionary<string, object>> result;
            try
            {
                result = TryExcecuteRead(query);
            }
            catch (SqlException e)
            {
                throw new DataInaccessibleException();
            }
            return result;
        }

        private ICollection<Dictionary<string, object>> TryExcecuteRead(string query)
        {
            ICollection<Dictionary<string, object>> result;
            using (SqlConnection connection = new SqlConnection(connectionString))
            {
                connection.Open();
                using (SqlCommand sqlCmd = new SqlCommand(query, connection))
                {
                    using (SqlDataReader reader = sqlCmd.ExecuteReader())
                    {
                        result = GenerateDictionary(reader);
                    }
                }
            }
            return result;
        }

        private ICollection<Dictionary<string, object>> GenerateDictionary(SqlDataReader reader)
        {
            ICollection<Dictionary<string, object>> rows = new List<Dictionary<string, object>>();
            while (reader.Read())
            {
                Dictionary<string, object> currentRow = Enumerable.Range(0, reader.FieldCount)
                 .ToDictionary(reader.GetName, reader.GetValue);
                rows.Add(currentRow);
            }
            return rows;
        }
    }
}
