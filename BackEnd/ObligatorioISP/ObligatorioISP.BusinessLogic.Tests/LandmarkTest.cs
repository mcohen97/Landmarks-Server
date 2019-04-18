using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.BusinessLogic.Exceptions;
using System.Collections.Generic;
using System.IO;

namespace ObligatorioISP.BusinessLogic.Tests
{
    [TestClass]
    public class LandmarkTest
    {
        [TestInitialize]
        public void StartUp() {
            File.Create("testImage.jpg");
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionWhenNameIsNullOrEmpty()
        {
            Landmark landmark = new Landmark("", 0.0, 0.0, "description", "iconPath");
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionWhenDescriptionIsNull() {
            Landmark landmark = new Landmark("title", 0.0, 0.0, null, "iconPath");
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionWhenPathDoesNotExist()
        {
            Landmark landmark = new Landmark("title", 0.0, 0.0, "description", "iconPath");
        }

        [TestMethod]
        public void ShouldCreateLandmarkIfDataIsValid() {
            Landmark landmark = new Landmark("title", 0.0, 0.0, "description", "testImage.jpg");
            Assert.AreEqual("title", landmark.Title);
            Assert.AreEqual("description", landmark.Description);

        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionIfImageListIsEmpty() {
            Landmark landmark = new Landmark("title", "description", new List<string>(), 0.0, 0.0);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionIfImageListHasUnexistentPath()
        {
            Landmark landmark = new Landmark("title", "description", new List<string>() { "iconImage"}, 0.0, 0.0);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionIfImageListIsNull()
        {
            Landmark landmark = new Landmark("title", "description", null, 0.0, 0.0);
        }

        [TestMethod]
        public void ShouldReturnTheImagesPathsWhenAsked() {
            Landmark landmark = new Landmark("title", 0.0, 0.0, "description", "testImage.jpg");
            List<string> images = landmark.Images;
            Assert.AreEqual(1, images.Count);
            Assert.AreEqual("testImage.jpg", images[0]);
        }
    }
}
