using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.BusinessLogic.Exceptions
{
    public class InvalidTourException: Exception
    {
        public InvalidTourException(string message) : base(message)
        {
        }
    }
}
