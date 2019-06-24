
using ObligatorioISP.Services.Contracts.Dtos;
using System.Collections.Generic;

namespace ObligatorioISP.Services.Contracts
{
    public interface IToursService
    {
        TourDto GetTourById(int id);
        ICollection<TourDto> GetToursWithinKmRange(double lat, double lng, double distance);
    }
}
