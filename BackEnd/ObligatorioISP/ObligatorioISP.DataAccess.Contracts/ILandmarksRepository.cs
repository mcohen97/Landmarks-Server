using ObligatorioISP.BusinessLogic;
using System.Collections.Generic;

namespace ObligatorioISP.DataAccess.Contracts
{
    public interface ILandmarksRepository
    {
        ICollection<Landmark> GetWithinZone(double centerLat, double centerLng, double distanceInKm, int offset = 0, int count = 50);

        ICollection<Landmark> GetTourLandmarks(int tourId);

        Landmark GetById(int id);
    }
}
