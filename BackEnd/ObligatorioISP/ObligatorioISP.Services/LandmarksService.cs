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
        private IImagesRepository images;

        public LandmarksService(ILandmarksRepository landmarksStorage, IImagesRepository imagesStorage) {
            landmarks = landmarksStorage;
            images = imagesStorage;
        }

        public ICollection<LandmarkDto> GetLandmarksOfTour(int id)
        {
            ICollection<Landmark> retrieved = landmarks.GetTourLandmarks(id);
            ICollection<LandmarkDto> dtos = GenerateDtos(retrieved);
            return dtos;
        }

        public ICollection<LandmarkDto> GetLandmarksWithinZone(double latitude, double longitude, double distance)
        {
            ICollection<Landmark> retrieved = landmarks.GetWithinZone(latitude, longitude, distance);
            ICollection<LandmarkDto> dtos = GenerateDtos(retrieved);
            return dtos;
        }
        private ICollection<LandmarkDto> GenerateDtos(ICollection<Landmark> retrievedLandmarks)
        {
            ICollection<LandmarkDto> result = new List<LandmarkDto>();

            foreach (Landmark current in retrievedLandmarks) {
                ICollection<string> currentImages = current.Images
                    .Select(path => images.GetImageInBase64(path))
                    .ToList();
                LandmarkDto dto = ConvertToDto(current, currentImages);
                result.Add(dto);
            }
            return result;
        }

        private LandmarkDto ConvertToDto(Landmark landmark, ICollection<string> images)
        {
            return new LandmarkDto()
            {
                Id = landmark.Id,
                Title = landmark.Title,
                Latitude = landmark.Latitude,
                Longitude = landmark.Longitude,
                Description = landmark.Description,
                ImagesBase64 = images
            };
        }
    }
}
