using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Microsoft.Extensions.Configuration;
using ObligatorioISP.Services.Contracts;
using System.Collections.Generic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.BusinessLogic;
using System.IO;
using System.Threading.Tasks;
using Microsoft.Extensions.Caching.Memory;

namespace ObligatorioISP.Services.Tests
{
    [TestClass]

    public class FirebaseNotificationServiceTest
    {
        private IProximityNotificationService testService;
        private Mock<ILandmarksRepository> landmarks;
        private MemoryCache tokenCache;

        [TestInitialize]
        public void SetUp()
        {
            Mock<IConfiguration> config = new Mock<IConfiguration>();
            config.Setup(c => c["Firebase:Url"]).Returns("https://fcm.googleapis.com");
            config.Setup(c => c["Firebase:ApplicationID"]).Returns("someAppId");
            config.Setup(c => c["Firebase:SenderID"]).Returns("someSenderId");
            config.Setup(c => c["ProximityNotifications:MaxDistance"]).Returns("0.5");
            config.Setup(c => c["ProximityNotifications:NotifMinimumIntervalHours"]).Returns("0.05");
            landmarks = new Mock<ILandmarksRepository>();
            tokenCache = new MemoryCache(new MemoryCacheOptions());
            testService = new FirebaseNotificationService(config.Object, landmarks.Object, tokenCache);
        }

        [TestMethod]
        public void ShouldNotifyLandmarkCloseIfExists()
        {
            landmarks.Setup(r => r.GetWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>(), It.IsAny<int>(), It.IsAny<int>())).Returns(GetFakeLandmarks());
            Task<bool> task = testService.NotifyIfCloseToLandmark("aToken", -34.9185678, -56.1674899);
            landmarks.Verify(r => r.GetWithinZone(-34.9185678, -56.1674899, It.IsAny<double>(), It.IsAny<int>(), It.IsAny<int>()));
            Assert.IsTrue(task.Result);
        }

        [TestMethod]
        public void ShouldReturnFalseIfNoLandmarksAreClose()
        {
            landmarks.Setup(r => r.GetWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>(), It.IsAny<int>(), It.IsAny<int>())).Returns(new List<Landmark>());
            Task<bool> task = testService.NotifyIfCloseToLandmark("aToken", -34.9185678, -56.1674899);
            landmarks.Verify(r => r.GetWithinZone(-34.9185678, -56.1674899, It.IsAny<double>(), It.IsAny<int>(), It.IsAny<int>()));
            Assert.IsFalse(task.Result);
        }

        [TestMethod]
        public void ShouldReturnFalseIfRecentlyNotified()
        {
            landmarks.Setup(r => r.GetWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>(), It.IsAny<int>(), It.IsAny<int>())).Returns(GetFakeLandmarks());
            Task<bool> task = testService.NotifyIfCloseToLandmark("aToken", -34.9185678, -56.1674899);
            bool firstAttempt = task.Result;
            task = testService.NotifyIfCloseToLandmark("aToken", -34.9185678, -56.1674899);
            bool inmediateAttempt = task.Result;
            Assert.IsTrue(firstAttempt);
            Assert.IsFalse(inmediateAttempt);
        }

        private List<Landmark> GetFakeLandmarks()
        {
            string image = "testImage.jpg";
            if (!File.Exists(image))
            {
                File.Create(image);
            }
            return new List<Landmark>() {
                new Landmark(1,"Landmark1",-34.892576 ,-56.155252,"Descripcion 1", image),
                new Landmark(2,"Landmark1",-34.893723 ,-56.165622,"Descripcion 2", image),
                new Landmark(3,"Landmark1",-34.905459 ,-56.184952,"Descripcion 3", image)};
        }
    }
}
