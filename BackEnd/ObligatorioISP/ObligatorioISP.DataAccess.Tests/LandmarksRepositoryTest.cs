using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Data;
using System.Data.Common;
using System.Data.SqlClient;
using System.IO;

namespace ObligatorioISP.DataAccess.Tests
{
    [TestClass]
    public class LandmarksRepositoryTest
    {
        private LandmarksRepository landmarks;
        private string serverString = $"Server=DESKTOP-JH1M2MF\\SQLSERVER_R14;";
        private string securityString = "Trusted_Connection=True;Integrated Security=True;";
        private string dbName = "LandmarksTestDB";

        [TestInitialize]
        public void StartUp()
        {
            SetUpDatabase();
        }


        [TestMethod]
        public void Test()
        {
            Assert.IsTrue(true);
        }

        private void SetUpDatabase()
        {

            if (!DbExists(dbName))
            {
                CreateDB(dbName);
            }
        }

        private bool DbExists(string database)
        {
            string connString = serverString + "Initial Catalog=master;" + securityString;
            string cmdText = "select * from master.dbo.sysdatabases where name=\'" + database + "\'";
            bool exists = false;
            using (SqlConnection client = new SqlConnection(connString))
            {
                client.Open();

                using (SqlCommand sqlCmd = new SqlCommand(cmdText, client))
                {

                    SqlDataReader reader = sqlCmd.ExecuteReader();

                    exists = reader.HasRows;

                    reader.Close();

                }
            }
            return exists;
        }

        private void CreateDB(string connectionString)
        {
            using (SqlConnection client = new SqlConnection(connectionString))
            {
                client.Open();
                string cmd = $"CREATE DATABASE {dbName} ; ";
                using (SqlCommand sqlCmd = new SqlCommand(cmd, client))
                {
                    sqlCmd.ExecuteNonQuery();
                }
                string script = File.ReadAllText(@"Database\create_tables.sql");
                using (SqlCommand sqlCmd = new SqlCommand(script, client))
                {
                    sqlCmd.ExecuteNonQuery();
                }
            }
        }
    }
}