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
        [ExpectedException(typeof(TourNotFoundException))]
        public void ShouldThrowExceptionIfTourIsUnexistent() {
            TourDto retrieved = tours.GetById(101);
        }

        [TestMethod]
        public void ShouldReturnTourWhoseStopsAreInRange() {
            double lat = -34.923844;
            double lng = -56.170590;
            double distance = 3;

            ICollection<TourDto> retrieved = tours.GetToursWithinKmRange(lat, lng, distance);
            Assert.AreEqual(1, retrieved.Count);
        }

        [TestMethod]
        public void ShouldReturnToursWhoseStopsAreInRange()
        {
            double lat = -34.923844;
            double lng = -56.170590;
            double distance = 20;

            ICollection<TourDto> retrieved = tours.GetToursWithinKmRange(lat, lng, distance);
            Assert.AreEqual(2, retrieved.Count);
        }

        [TestMethod]
        public void ShouldReturnNoToursIfNoneIsWithinRange() {
            double lat = -34.923844;
            double lng = -56.170590;
            double distance = 1;

            ICollection<TourDto> retrieved = tours.GetToursWithinKmRange(lat, lng, distance);
            Assert.AreEqual(0, retrieved.Count);
        }
    }
}
