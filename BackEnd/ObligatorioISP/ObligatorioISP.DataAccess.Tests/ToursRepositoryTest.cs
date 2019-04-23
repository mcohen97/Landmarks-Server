using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.DataAccess.Contracts.Dtos;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Tests
{
    [TestClass]
    public class ToursRepositoryTest
    {
        private SqlServerLandmarksRepository landmarks;
        private SqlServerToursRepository tours;
        private TestDatabaseManager testData;

        [TestInitialize]
        public void SetUp() {
            testData = new TestDatabaseManager();
            testData.SetUpDatabase();
            testData.LoadTestData();
            landmarks = new SqlServerLandmarksRepository(testData.ConnectionString, testData.ImagesPath, testData.AudiosPath);
            tours = new SqlServerToursRepository(testData.ConnectionString,landmarks);
        }

        [TestMethod]
        public void ShouldReturnTourGivenExistingId() {
            TourDto retrieved = tours.GetById(1);
            Assert.AreEqual(1, retrieved.Id);
        }

        [TestMethod]
        public void ShouldReturnToursWhoseCenterIsWithinRange() {
            double lat = -34.923844;
            double lng = -56.170590;

            ICollection<TourDto> retrieved = tours.GetToursWithinKmRange(lat, lng, 2);
            Assert.AreEqual(2, retrieved.Count);
        }
    }
}
