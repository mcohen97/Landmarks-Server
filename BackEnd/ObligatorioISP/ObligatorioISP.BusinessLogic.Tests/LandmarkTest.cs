using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.BusinessLogic.Exceptions;
using System.Collections.Generic;
using System.IO;

namespace ObligatorioISP.BusinessLogic.Tests
{
    [TestClass]
    public class LandmarkTest
    {
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
            File.Create("testImage.jpg");
            Landmark landmark = new Landmark("title", 0.0, 0.0, "description", "testImage.jpg");
        }

        [TestMethod]
        public void ShouldReturnTheImagesPathsWhenAsked() {
            Landmark landmark = new Landmark("title", 0.0, 0.0, "description", "testImage.jpg");
            List<string> images = landmark.Images;
            Assert.AreEqual(1, images.Count);
            Assert.AreEqual("testImage.jpg", images.Images[0]);
        }
    }
}
