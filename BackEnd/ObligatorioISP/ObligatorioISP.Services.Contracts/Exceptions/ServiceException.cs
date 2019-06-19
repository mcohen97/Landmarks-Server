using System;

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
