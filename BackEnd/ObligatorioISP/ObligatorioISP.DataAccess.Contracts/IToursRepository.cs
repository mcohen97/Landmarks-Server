using ObligatorioISP.BusinessLogic;
using System.Collections.Generic;

namespace ObligatorioISP.DataAccess.Contracts
{
    public interface IToursRepository
    {
        Tour GetById(int id);
        ICollection<Tour> GetToursWithinKmRange(double centerLat, double centerLng, double rangeInKm);
    }
}
