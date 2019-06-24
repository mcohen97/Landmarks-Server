using System;

namespace ObligatorioISP.DataAccess.Contracts.Exceptions
{
    public class TourNotFoundException : EntityNotFoundException
    {
        public TourNotFoundException() : base("Tour not found")
        {
        }
    }
}
