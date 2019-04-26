using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.Services.Contracts;
using System.Collections.Generic;
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
            fakeToursStorage.Setup(r => r.GetToursWithinKmRange(It.IsAny<int>(), It.IsAny<int>(), It.IsAny<int>()))
                .Returns(GetFakeTours());
            fakeToursStorage.Setup(r => r.GetById(It.IsAny<int>())).Returns((int x) => GetFakeTours().First(t => t.Id == x));
            fakeLandmarksStorage = new Mock<ILandmarksRepository>();
            service = new ToursService(fakeToursStorage.Object, fakeLandmarksStorage.Object);
        }

        private ICollection<Tour> GetFakeTours()
        {
            List<Landmark> testLandmarks = GetFakeLandmarks() as List<Landmark>;
            return new List<Tour>() {
                new Tour(1,"Tour 1",testLandmarks),
                new Tour(2,"Tour 2", new List<Landmark>(){testLandmarks[0],testLandmarks[4],testLandmarks[2] }),
                new Tour(3,"Tour 3", new List<Landmark>(){testLandmarks[1],testLandmarks[3] })
            };
        }

        private ICollection<Landmark> GetFakeLandmarks()
        {
            ICollection<Landmark> sampleList = new List<Landmark>() {
                new Landmark(1, "Landmark 1",-34.912126,-56.167282,"Description 1", ""),
                new Landmark(2,"Landmark 2",-34.912900,-56.162263,"Description 2",""),
                new Landmark(3,"Landmark 3",-34.914202,-56.157930,"Description 3",""),
                new Landmark(4,"Landmark 4", -34.910866,-56.183353,"Description 4","")
            };
            return sampleList;
        }
    }
}
