

using System;

namespace ObligatorioISP.BusinessLogic.Exceptions
{
    public class InvalidLandmarkException : Exception
    {
        public InvalidLandmarkException(string message) : base(message)
        {
        }
    }
}
