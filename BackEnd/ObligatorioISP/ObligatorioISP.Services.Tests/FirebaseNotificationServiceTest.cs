using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Microsoft.Extensions.Configuration;
using ObligatorioISP.Services.Contracts;
using System;
using System.Collections.Generic;
using System.Text;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.BusinessLogic;
using System.IO;

namespace ObligatorioISP.Services.Tests
{
    [TestClass]

    public class FirebaseNotificationServiceTest
    {
        private IProximityNotificationService testService;
        private Mock<ILandmarksRepository> landmarks;

        [TestInitialize]
        public void SetUp() {
            Mock<IConfiguration> config = new Mock<IConfiguration>();
            landmarks = new Mock<ILandmarksRepository>();
            testService = new FirebaseNotificationService(config.Object, landmarks.Object);
        }

        [TestMethod]
        public void ShouldReturnClosestLandmarkIfExists() {
            landmarks.Setup(r => r.GetWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>())).Returns(GetFakeLandmarks());
            testService.NotifyIfCloseToLandmark("aToken", -34.9185678, -56.1674899);
            landmarks.Verify(r => r.GetWithinZone(-34.9185678, -56.1674899, It.IsAny<double>()));
        }

        private List<Landmark> GetFakeLandmarks()
        {
            string image = "testImage.jpg";
            if (!File.Exists(image)) {
                File.Create(image);
            }
            return new List<Landmark>() {
                new Landmark(1,"Landmark1",-34.892576 ,-56.155252,"Descripcion 1", image),
                new Landmark(2,"Landmark1",-34.893723 ,-56.165622,"Descripcion 2", image),
                new Landmark(3,"Landmark1",-34.905459 ,-56.184952,"Descripcion 3", image)};
        }
    }
}
