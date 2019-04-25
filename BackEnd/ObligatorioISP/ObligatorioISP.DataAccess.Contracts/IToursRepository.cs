using ObligatorioISP.DataAccess.Contracts.Dtos;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Contracts
{
    public interface IToursRepository
    {
        TourDto GetById(int id);
        ICollection<TourDto> GetToursWithinKmRange(double centerLat, double centerLng, double rangeInKm);
    }
}
