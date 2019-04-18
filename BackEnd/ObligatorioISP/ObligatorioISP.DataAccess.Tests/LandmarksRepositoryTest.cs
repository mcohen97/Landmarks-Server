using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Data.SqlClient;
using System.IO;

namespace ObligatorioISP.DataAccess.Tests
{
    [TestClass]
    public class LandmarksRepositoryTest
    {
        private SqlServerLandmarksRepository landmarks;
        private string serverString = $"Server=DESKTOP-JH1M2MF\\SQLSERVER_R14;";
        private string securityString = "Trusted_Connection=True;Integrated Security=True;";
        private string dbName = "LandmarksTestDB";

        [TestInitialize]
        public void StartUp()
        {
            SetUpDatabase();
            string connString = serverString + $"Initial Catalog={dbName};" + securityString;
            landmarks = new SqlServerLandmarksRepository(connString);
        }

        [TestMethod]
        public void ShouldGiveLandmarksWithinBounds()
        {
            double bottomLeftLat = -34.923844;
            double bottomLeftLng = -56.170590;
            double topRightLat = -34.908501;
            double topRightLng = -56.155019;

            ICollection<LandmarkDto> withinBounds = landmarks.GetWithinCoordenates(bottomLeftLat, bottomLeftLng, topRightLat, topRightLng);
            Assert.AreEqual(3, withinBounds.Count);
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
            string cmdText = "SELECT * FROM master.dbo.sysdatabases WHERE NAME=\'" + database + "\'";
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
                string createScript = File.ReadAllText(@"Database\create_tables.sql");
                using (SqlCommand sqlCmd = new SqlCommand(createScript, client))
                {
                    sqlCmd.ExecuteNonQuery();
                }
                string testDataScript = File.ReadAllText(@"Database\create_test_data.sql");
                using (SqlCommand sqlCmd = new SqlCommand(testDataScript, client))
                {
                    sqlCmd.ExecuteNonQuery();
                }
            }
        }
    }
}