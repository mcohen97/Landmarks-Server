using System;

namespace ObligatorioISP.DataAccess.Contracts.Exceptions
{
    public class TourNotFoundException: Exception
    {
        public TourNotFoundException(string message) : base(message) {
        }
    }
}
