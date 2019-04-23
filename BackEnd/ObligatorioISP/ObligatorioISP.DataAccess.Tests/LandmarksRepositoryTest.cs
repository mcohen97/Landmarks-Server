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
            double bottomLeftLat = -34.923844;
            double bottomLeftLng = -56.170590;
            double topRightLat = -34.908501;
            double topRightLng = -56.155019;

            ICollection<LandmarkDto> withinBounds = landmarks.GetWithinCoordenates(bottomLeftLat, bottomLeftLng, topRightLat, topRightLng);
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