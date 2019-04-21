using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using Moq;
using ObligatorioISP.BusinessLogic.Exceptions;
using System.IO;

namespace ObligatorioISP.BusinessLogic.Tests
{
    [ExcludeFromCodeCoverage]
    [TestClass]
    public class TourTest
    {
        private ICollection<Landmark> fakeLandmarks;
        [TestInitialize]
        public void StartUp() {
            fakeLandmarks = GetFakeLandmarks();
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidTourException))]
        public void ShouldThrowExceptionWhenIdIsNegative() {
            Tour testTour = new Tour(-1, "Tour 1", fakeLandmarks);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidTourException))]
        public void ShouldThrowExceptionWhenTitleEmpty()
        {
            Tour testTour = new Tour(1, " ", fakeLandmarks);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidTourException))]
        public void ShouldThrowExceptionWhenToursListIsNull()
        {
            Tour testTour = new Tour(1, "Tour 1", null);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidTourException))]
        public void ShouldThrowExceptionWhenToursListIsEmpty()
        {
            Tour testTour = new Tour(1, "Tour 1", new List<Landmark>());
        }

        [TestMethod]
        public void ShouldHaveSameDataGivenInConstructor() {
            Tour testTour = new Tour(1, "Tour 1", fakeLandmarks);
            Assert.AreEqual(1, testTour.Id);
            Assert.AreEqual("Tour 1", testTour.Title);
            Assert.AreEqual(3, fakeLandmarks.Count);
        }

        private ICollection<Landmark> GetFakeLandmarks()
        {
            File.Create("1_1.jpg");
            Mock<Landmark> fake1 = new Mock<Landmark>("Landmark 1", -34.912126, -56.167282, "Description 1", "1_1.jpg");
            Mock<Landmark> fake2 = new Mock<Landmark>("Landmark 2", -34.912127, -56.167281, "Description 2", "1_1.jpg");
            Mock<Landmark> fake3 = new Mock<Landmark>("Landmark 3", -34.912128, -56.167284, "Description 3", "1_1.jpg");
            return new List<Landmark>() { fake1.Object, fake2.Object, fake3.Object };
        }
    }
}
