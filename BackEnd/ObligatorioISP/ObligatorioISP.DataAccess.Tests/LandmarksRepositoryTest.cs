using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.DataAccess.Contracts.Dtos;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Data.SqlClient;
using System.Diagnostics.CodeAnalysis;
using System.IO;

namespace ObligatorioISP.DataAccess.Tests
{
    [ExcludeFromCodeCoverage]
    [TestClass]
    public class LandmarksRepositoryTest
    {
        private SqlServerLandmarksRepository landmarks;
        private string serverString = $"Server=DESKTOP-JH1M2MF\\SQLSERVER_R14;";
        private string securityString = "Trusted_Connection=True;Integrated Security=True;";
        private string dbName = "LandmarksTestDB";
        private string imagesPath = "Images";
        private string audiosPath = "Audios";

        [TestInitialize]
        public void StartUp()
        {
            string connString = serverString + $"Initial Catalog={dbName};" + securityString;
            SetUpDatabase(connString);
            landmarks = new SqlServerLandmarksRepository(connString,imagesPath,audiosPath);
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

        private void SetUpDatabase(string finalConnString)
        {

            if (!DbExists(dbName))
            {
                CreateDB(dbName);
            }
            LoadDatabase(finalConnString);
            CreateImageFiles(finalConnString,imagesPath);
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
               
            }
        }

        private void LoadDatabase(string connectionString)
        {
            using (SqlConnection client = new SqlConnection(connectionString))
            {
                client.Open();
                string createScript = File.ReadAllText(@"..\..\..\..\Database\create_tables.sql");
                using (SqlCommand sqlCmd = new SqlCommand(createScript, client))
                {
                    sqlCmd.ExecuteNonQuery();
                }
                string testDataScript = File.ReadAllText(@"..\..\..\..\Database\create_test_data.sql");
                using (SqlCommand sqlCmd = new SqlCommand(testDataScript, client))
                {
                    sqlCmd.ExecuteNonQuery();
                }
            }
              
        }

        private void CreateImageFiles(string connectionString,string imagesPath)
        {
            using (SqlConnection client = new SqlConnection(connectionString))
            {
                client.Open();
                string getIdsScript = "SELECT * FROM LandmarkImages;";
                using (SqlCommand sqlCmd = new SqlCommand(getIdsScript, client))
                {
                    using (SqlDataReader reader = sqlCmd.ExecuteReader())
                    {

                        while (reader.Read())
                        {
                            string landmarkId = reader["LANDMARK_ID"].ToString();
                            string id = reader["ID"].ToString();
                            string extension = reader["EXTENSION"].ToString();
                            File.Create($"{imagesPath}/{landmarkId}_{id}.{extension}");
                        }
                    }
                }
            }
        }
    }
}