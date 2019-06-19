using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Exceptions;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.Services.Contracts.Exceptions;
using System.Collections.Generic;

namespace ObligatorioISP.Services
{
    public class LandmarksService : ILandmarksService
    {
        private ILandmarksRepository landmarks;

        public LandmarksService(ILandmarksRepository landmarksStorage)
        {
            landmarks = landmarksStorage;
        }

        public ICollection<LandmarkDto> GetLandmarksOfTour(int id)
        {
            try
            {
                return TryGetLandmarksOfTour(id);
            }
            catch (EntityNotFoundException e1)
            {
                throw new ServiceException(e1.Message, ErrorType.ENTITY_NOT_FOUND);
            }
            catch (DataInaccessibleException e2)
            {
                throw new ServiceException(e2.Message, ErrorType.DATA_INACCESSIBLE);
            }
            catch (CorruptedDataException e3)
            {
                throw new ServiceException(e3.Message, ErrorType.DATA_CORRUPTED);
            }
        }

        private ICollection<LandmarkDto> TryGetLandmarksOfTour(int id)
        {
            ICollection<Landmark> retrieved = landmarks.GetTourLandmarks(id);
            ICollection<LandmarkDto> dtos = GetLandmarkDtos(retrieved);
            return dtos;
        }

        public ICollection<LandmarkDto> GetLandmarksWithinZone(double latitude, double longitude, double distance)
        {
            try
            {
                return TryGetLandmarksWithinZone(latitude, longitude, distance);
            }
            catch (DataInaccessibleException e)
            {
                throw new ServiceException(e.Message, ErrorType.DATA_INACCESSIBLE);
            }
            catch (CorruptedDataException e2)
            {
                throw new ServiceException(e2.Message, ErrorType.DATA_CORRUPTED);
            }
        }

        private ICollection<LandmarkDto> TryGetLandmarksWithinZone(double latitude, double longitude, double distance)
        {
            ICollection<Landmark> retrieved = landmarks.GetWithinZone(latitude, longitude, distance);
            ICollection<LandmarkDto> dtos = GetLandmarkDtos(retrieved);
            return dtos;
        }

        public LandmarkDto GetLandmarkById(int id)
        {
            try
            {
                return TryGetLandmarkById(id);
            }
            catch (EntityNotFoundException e1)
            {
                throw new ServiceException(e1.Message, ErrorType.ENTITY_NOT_FOUND);
            }
            catch (DataInaccessibleException e2)
            {
                throw new ServiceException(e2.Message, ErrorType.DATA_INACCESSIBLE);
            }
            catch (CorruptedDataException e3)
            {
                throw new ServiceException(e3.Message, ErrorType.DATA_CORRUPTED);
            }
        }

        private LandmarkDto TryGetLandmarkById(int id)
        {
            Landmark retrieved = landmarks.GetById(id);
            ICollection<string> currentImages = retrieved.Images;

            ICollection<string> currentAudios = retrieved.Audios;

            LandmarkDto dto = ConvertToDto(retrieved);
            return dto;
        }

        private ICollection<LandmarkDto> GetLandmarkDtos(ICollection<Landmark> retrievedLandmarks)
        {
            ICollection<LandmarkDto> result = new List<LandmarkDto>();
            foreach (Landmark current in retrievedLandmarks)
            {
                LandmarkDto dto = ConvertToDto(current);
                result.Add(dto);
            }
            return result;
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
                ImageFiles = landmark.Images,
                AudioFiles = landmark.Audios
            };
        }

    }
}
