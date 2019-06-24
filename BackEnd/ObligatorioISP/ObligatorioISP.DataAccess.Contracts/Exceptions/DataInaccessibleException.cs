using System;

namespace ObligatorioISP.DataAccess.Contracts.Exceptions
{
    public class DataInaccessibleException : Exception
    {
        public DataInaccessibleException() : base("Can't access data")
        {
        }
    }
}
