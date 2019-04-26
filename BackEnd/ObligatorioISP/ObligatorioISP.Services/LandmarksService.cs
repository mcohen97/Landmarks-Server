using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ObligatorioISP.Services
{
    public class LandmarksService: ILandmarksService
    {
        private ILandmarksRepository landmarks;

        public LandmarksService(ILandmarksRepository landmarksStorage) {
            landmarks = landmarksStorage;
        }

        public ICollection<LandmarkDto> GetLandmarksOfTour(int id)
        {
            ICollection<Landmark> retrieved =landmarks.GetTourLandmarks(id);
            return retrieved.Select(l => ConvertToDto(l)).ToList();
        }

        public ICollection<LandmarkDto> GetLandmarksWithinZone(double latitude, double longitude, double distance)
        {
            ICollection<Landmark> retrieved = landmarks.GetWithinZone(latitude, longitude, distance);
            return retrieved.Select(l => ConvertToDto(l)).ToList();
        }

        private LandmarkDto ConvertToDto(Landmark landmark)
        {
            return new LandmarkDto()
            {
                Id = landmark.Id,
                Title = landmark.Title,
                Latitude = landmark.Latitude,
                Longitude = landmark.Longitude,
                Description = landmark.Description,
                //ImageBase64 = landmark.Images.First()
            };
        }
    }
}
