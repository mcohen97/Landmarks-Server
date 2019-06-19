using Exception = System.Exception;

namespace ObligatorioISP.BusinessLogic.Exceptions
{
    public class InvalidLandmarkException : Exception
    {
        public InvalidLandmarkException(string message) : base(message)
        {
        }
    }
}