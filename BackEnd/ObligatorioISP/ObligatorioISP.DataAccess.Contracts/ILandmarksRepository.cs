using ObligatorioISP.BusinessLogic;
using System.Collections.Generic;

namespace ObligatorioISP.DataAccess.Contracts
{
    public interface ILandmarksRepository
    {
        ICollection<Landmark> GetWithinZone(double centerLat, double centerLng, double distanceInKm);

        ICollection<Landmark> GetTourLandmarks(int tourId);

        Landmark GetById(int id);
    }
}
