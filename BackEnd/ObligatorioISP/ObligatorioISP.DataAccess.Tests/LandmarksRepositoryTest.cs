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
        private TestDatabaseManager testData;


        [TestInitialize]
        public void StartUp()
        {
            testData = new TestDatabaseManager();
            testData.SetUpDatabase();
            testData.LoadTestData();
            landmarks = new SqlServerLandmarksRepository(testData.ConnectionString,testData.ImagesPath,testData.AudiosPath);
        }

        [TestMethod]
        public void ShouldGiveLandmarksWithinBounds()
        {
            double centerLat = -34.923844;
            double centerLng = -56.170590;

            ICollection<LandmarkDto> withinBounds = landmarks.GetWithinZone(centerLat, centerLng, 2);
            Assert.AreEqual(3, withinBounds.Count);
        }

        [TestMethod]
        public void ShouldGiveLandmarksCoveredByTour() {
            int tourId = 1;
            ICollection<LandmarkDto> fromTour = landmarks.GetTourLandmarks(tourId);
            Assert.AreEqual(3, fromTour.Count);
        }
    }
}