using ObligatorioISP.DataAccess.Contracts.Dtos;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Contracts
{
    public interface ILandmarksRepository
    {
        ICollection<LandmarkDto> GetWithinZone(double centerLat, double centerLng, double distanceInKm);

        ICollection<LandmarkDto> GetTourLandmarks(int tourId);
    }
}
