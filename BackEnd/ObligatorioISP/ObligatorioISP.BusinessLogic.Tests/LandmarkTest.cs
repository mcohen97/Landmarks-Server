using Microsoft.VisualStudio.TestTools.UnitTesting;

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
            Landmark landmark = new Landmark("", 0.0, 0.0, "description", "testImage.jpg");
        }
    }
}
