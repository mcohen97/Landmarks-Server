using Exception =System.Exception;

namespace ObligatorioISP.BusinessLogic.Exceptions
{
    public class InvalidTourException: Exception
    {
        public InvalidTourException(string message) : base(message)
        {
        }
    }
}
