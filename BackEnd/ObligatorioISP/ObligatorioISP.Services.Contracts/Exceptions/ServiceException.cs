using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.Services.Contracts.Exceptions
{
    public class ServiceException : Exception
    {
        public ErrorType Error { get; set; }
        public ServiceException(string message, ErrorType type) : base(message)
        {
            Error = type;
        }
    }
}
