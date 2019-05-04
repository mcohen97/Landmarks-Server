using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.IO;
using System.Text;

namespace ObligatorioISP.DataAccess.Tests
{
    public class TestDatabaseManager
    {
        private string serverString = $"Server=DESKTOP-JH1M2MF\\SQLSERVER_R14;";
        private string securityString = "Trusted_Connection=True;Integrated Security=True;";
        public string dbName = "LandmarksTestDB";
        public string ImagesPath { get; private set; }
        public string AudiosPath { get; private set; }
        public string ConnectionString { get; private set; }

        public TestDatabaseManager() {
            ConnectionString = serverString + $"Initial Catalog={dbName};" + securityString;
            ImagesPath = "Images";
            AudiosPath = "Audios";
        }

        public void SetUpDatabase()
        {
            string finalConnString = ConnectionString;

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

            }
        }

        public void LoadTestData()
        {
            using (SqlConnection client = new SqlConnection(ConnectionString))
            {
                client.Open();
                ResetDatabase(client);
                string testDataScript = File.ReadAllText(@"..\..\..\..\Database\create_test_data.sql");
                using (SqlCommand sqlCmd = new SqlCommand(testDataScript, client))
                {
                    sqlCmd.ExecuteNonQuery();
                }
            }
            CreateImageFiles(ConnectionString, ImagesPath);
            CreateAudioFiles(ConnectionString, AudiosPath);
        }

        private void ResetDatabase(SqlConnection client)
        {
            string createScript = File.ReadAllText(@"..\..\..\..\Database\create_tables.sql");
            using (SqlCommand sqlCmd = new SqlCommand(createScript, client))
            {
                sqlCmd.ExecuteNonQuery();
            }
            string createFunctions = File.ReadAllText(@"..\..\..\..\Database\stored_functions.sql");
            string[] functionCommands = createFunctions.Split("GO");
            for (int i = 0; i < functionCommands.Length; i++)
            {
                if (!String.IsNullOrEmpty(functionCommands[i])) {
                    using (SqlCommand sqlCmd = new SqlCommand(functionCommands[i], client))
                    {
                        sqlCmd.ExecuteNonQuery();
                    }
                }
            }
        }

        private void CreateImageFiles(string connectionString, string imagesPath)
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
                            string fullPath = $"{imagesPath}/{landmarkId}_{id}.{extension}";
                            if (!File.Exists(fullPath)) {
                                File.Create(fullPath);
                            }
                        }
                    }
                }
            }
        }

        private void CreateAudioFiles(string connectionString, string audiosPath)
        {
            using (SqlConnection client = new SqlConnection(connectionString))
            {
                client.Open();
                string getIdsScript = "SELECT * FROM LandmarkAudios;";
                using (SqlCommand sqlCmd = new SqlCommand(getIdsScript, client))
                {
                    using (SqlDataReader reader = sqlCmd.ExecuteReader())
                    {

                        while (reader.Read())
                        {
                            string landmarkId = reader["LANDMARK_ID"].ToString();
                            string id = reader["ID"].ToString();
                            string extension = "mp3";
                            string fullPath = $"{audiosPath}/{landmarkId}_{id}.{extension}";
                            if (!File.Exists(fullPath))
                            {
                                File.Create(fullPath);
                            }
                        }
                    }
                }
            }
        }

    }
}
