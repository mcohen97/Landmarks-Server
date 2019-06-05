using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Microsoft.Extensions.Configuration;
using ObligatorioISP.Services.Contracts;
using System;
using System.Collections.Generic;
using System.Text;
using ObligatorioISP.DataAccess.Contracts;

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
    }
}
