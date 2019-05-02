using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Exceptions;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.Services.Contracts.Exceptions;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace ObligatorioISP.Services.Tests
{
    [TestClass]
    public class ToursServiceTest
    {
        private Mock<IToursRepository> fakeToursStorage;
        private Mock<ILandmarksRepository> fakeLandmarksStorage;
        private IToursService service;

        [TestInitialize]
        public void StartUp() {
            fakeToursStorage = new Mock<IToursRepository>();
            fakeToursStorage.Setup(r => r.GetToursWithinKmRange(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>()))
                .Returns(GetFakeTours());
            fakeToursStorage.Setup(r => r.GetById(It.IsAny<int>())).Returns((int x) => GetFakeTours().First(t => t.Id == x));
            fakeLandmarksStorage = new Mock<ILandmarksRepository>();
            service = new ToursService(fakeToursStorage.Object, fakeLandmarksStorage.Object);
        }

        [TestMethod]
        public void ShouldReturnToursWithinRange() {
            double lat= -34.912126;
            double lng= -56.167282;
            double distance=2;
            ICollection<TourDto> retrieved =service.GetToursWithinKmRange(lat, lng, distance);

            fakeToursStorage.Verify(r => r.GetToursWithinKmRange(lat, lng, distance), Times.Once);
            Assert.AreEqual(GetFakeTours().Count, retrieved.Count);
        }

        [TestMethod]
        public void ShouldReturnTourWithTheId() {
            int id = 3;

            TourDto retrieved = service.GetTourById(id);

            fakeToursStorage.Verify(r => r.GetById(id), Times.Once);
            Assert.AreEqual(id,retrieved.Id);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShoulFailWhenCantAccessDataGetToursWithinKmRange() {
            double lat = -34.912126;
            double lng = -56.167282;
            double distance = 2;
            fakeToursStorage.Setup(r => r.GetToursWithinKmRange(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>()))
                .Throws(new DataInaccessibleException());

            service.GetToursWithinKmRange(lat, lng, distance);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShouldFailWhenCantAccessDataGetTourById() { 
            int id = 3;

            fakeToursStorage.Setup(r => r.GetById(It.IsAny<int>()))
                .Throws(new DataInaccessibleException());

            service.GetTourById(id);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShouldFailWhenDataIsCorruptedGetToursWithinKmRange() {
            double lat = -34.912126;
            double lng = -56.167282;
            double distance = 2;
            fakeToursStorage.Setup(r => r.GetToursWithinKmRange(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>()))
                .Throws(new CorruptedDataException());

            service.GetToursWithinKmRange(lat, lng, distance);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShouldFailWhenDataIsCorruptedGetTourById() {
            int id = 3;

            fakeToursStorage.Setup(r => r.GetById(It.IsAny<int>()))
                .Throws(new CorruptedDataException());

            service.GetTourById(id);
        }

        private ICollection<Tour> GetFakeTours()
        {
            List<Landmark> testLandmarks = GetFakeLandmarks() as List<Landmark>;
            return new List<Tour>() {
                new Tour(1,"Tour 1",testLandmarks),
                new Tour(2,"Tour 2", new List<Landmark>(){testLandmarks[0],testLandmarks[3],testLandmarks[2] }),
                new Tour(3,"Tour 3", new List<Landmark>(){testLandmarks[1],testLandmarks[3] })
            };
        }

        private ICollection<Landmark> GetFakeLandmarks()
        {
            string testImagePath = "testImage.jpg";
            if (!File.Exists(testImagePath)) {
                File.Create(testImagePath);
            }

            ICollection<Landmark> sampleList = new List<Landmark>() {
                new Landmark(1, "Landmark 1",-34.912126,-56.167282,"Description 1", testImagePath),
                new Landmark(2,"Landmark 2",-34.912900,-56.162263,"Description 2",testImagePath),
                new Landmark(3,"Landmark 3",-34.914202,-56.157930,"Description 3",testImagePath),
                new Landmark(4,"Landmark 4", -34.910866,-56.183353,"Description 4",testImagePath)
            };
            return sampleList;
        }
    }
}
