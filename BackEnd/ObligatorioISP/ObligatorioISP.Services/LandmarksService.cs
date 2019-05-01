using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Exceptions;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.Services.Contracts.Exceptions;
using System;
using System.Collections.Generic;
using System.Linq;

namespace ObligatorioISP.Services
{
    public class LandmarksService : ILandmarksService
    {
        private ILandmarksRepository landmarks;
        private IImagesRepository images;
        private IAudiosRepository audios;

        public LandmarksService(ILandmarksRepository landmarksStorage, IImagesRepository imagesStorage, IAudiosRepository audiosStorage)
        {
            landmarks = landmarksStorage;
            images = imagesStorage;
            audios = audiosStorage;
        }

        public ICollection<LandmarkSummarizedDto> GetLandmarksOfTour(int id)
        {
            try
            {
                return TryGetLandmarksOfTour(id);
            }
            catch (DataInaccessibleException e1)
            {
                throw new ServiceException(e1.Message, ErrorType.DATA_INACCESSIBLE);
            }
            catch (CorruptedDataException e2) {
                throw new ServiceException(e2.Message, ErrorType.DATA_CORRUPTED);
            }
        }

        private ICollection<LandmarkSummarizedDto> TryGetLandmarksOfTour(int id)
        {
            ICollection<Landmark> retrieved = landmarks.GetTourLandmarks(id);
            ICollection<LandmarkSummarizedDto> dtos = GetSummarizedDtos(retrieved);
            return dtos;
        }

        public ICollection<LandmarkSummarizedDto> GetLandmarksWithinZone(double latitude, double longitude, double distance)
        {
            try {
                return TryGetLandmarksWithinZone(latitude, longitude, distance);
            }
            catch (DataInaccessibleException e) {
                throw new ServiceException(e.Message, ErrorType.DATA_INACCESSIBLE);
            }
            catch (CorruptedDataException e2)
            {
                throw new ServiceException(e2.Message, ErrorType.DATA_CORRUPTED);
            }
        }

        private ICollection<LandmarkSummarizedDto> TryGetLandmarksWithinZone(double latitude, double longitude, double distance)
        {
            ICollection<Landmark> retrieved = landmarks.GetWithinZone(latitude, longitude, distance);
            ICollection<LandmarkSummarizedDto> dtos = GetSummarizedDtos(retrieved);
            return dtos;
        }

        public LandmarkDetailedDto GetLandmarkById(int id)
        {
            try {
                return TryGetLandmarkById(id);
            }
            catch (DataInaccessibleException e) {
                throw new ServiceException(e.Message, ErrorType.DATA_INACCESSIBLE);
            }
            catch (CorruptedDataException e2)
            {
                throw new ServiceException(e2.Message, ErrorType.DATA_CORRUPTED);
            }
        }

        private LandmarkDetailedDto TryGetLandmarkById(int id)
        {
            Landmark retrieved = landmarks.GetById(id);
            ICollection<string> currentImages = retrieved.Images
                                                .Select(path => images.GetImageInBase64(path))
                                                .ToList();
            ICollection<string> currentAudios = retrieved.Audios
                                                .Select(path => audios.GetAudioInBase64(path))
                                                .ToList();
            LandmarkDetailedDto dto = ConvertToDto(retrieved, currentImages, currentAudios);
            return dto;
        }

        private ICollection<LandmarkSummarizedDto> GetSummarizedDtos(ICollection<Landmark> retrievedLandmarks)
        {
            ICollection<LandmarkSummarizedDto> result = new List<LandmarkSummarizedDto>();
            foreach (Landmark current in retrievedLandmarks)
            {
                string icon = images.GetImageInBase64(current.Icon);
                LandmarkSummarizedDto dto = ConvertToDto(current,icon);
                result.Add(dto);
            }
            return result;
        }

        private LandmarkSummarizedDto ConvertToDto(Landmark landmark, string icon)
        {
            return new LandmarkSummarizedDto()
            {
                Id = landmark.Id,
                Title = landmark.Title,
                Latitude = landmark.Latitude,
                Longitude = landmark.Longitude,
                IconBase64 = icon
            };
        }

        private LandmarkDetailedDto ConvertToDto(Landmark landmark, ICollection<string> images, ICollection<string> audios)
        {
            return new LandmarkDetailedDto()
            {
                Id = landmark.Id,
                Title = landmark.Title,
                Latitude = landmark.Latitude,
                Longitude = landmark.Longitude,
                Description = landmark.Description,
                ImagesBase64 = images,
                AudiosBase64 = audios
            };
        }

    }
}
