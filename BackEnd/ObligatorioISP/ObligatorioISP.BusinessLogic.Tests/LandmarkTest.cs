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
    }
}
