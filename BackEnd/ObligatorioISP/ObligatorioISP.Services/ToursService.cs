using System.Collections.Generic;
using System.Linq;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Exceptions;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.Services.Contracts.Exceptions;

namespace ObligatorioISP.Services
{
    public class ToursService : IToursService
    {
        private IToursRepository tours;


        public ToursService(IToursRepository toursStorage)
        {
            tours = toursStorage;
        }

        public TourDto GetTourById(int id)
        {
            try
            {
                return TryGetTourById(id);
            }
            catch (TourNotFoundException e1)
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

        private TourDto TryGetTourById(int id)
        {
            Tour retrievedTour = tours.GetById(id);
            TourDto dto = BuildDto(retrievedTour);
            return dto;
        }

        public ICollection<TourDto> GetToursWithinKmRange(double lat, double lng, double distance)
        {
            try
            {
                return TryGetToursWithinKmRange(lat, lng, distance);
            }
            catch (DataInaccessibleException e1)
            {
                throw new ServiceException(e1.Message, ErrorType.DATA_INACCESSIBLE);
            }
            catch (CorruptedDataException e2)
            {
                throw new ServiceException(e2.Message, ErrorType.DATA_CORRUPTED);
            }
        }

        private ICollection<TourDto> TryGetToursWithinKmRange(double lat, double lng, double distance)
        {
            ICollection<Tour> retrievedTours = tours.GetToursWithinKmRange(lat, lng, distance);
            return retrievedTours.Select(t => BuildDto(t)).ToList();
        }

        private TourDto BuildDto(Tour retrieved)
        {
            TourDto conversion = new TourDto
            {
                Id = retrieved.Id,
                Title = retrieved.Title,
                Description = retrieved.Description,
                LandmarksIds = retrieved.Landmarks.Select(t => t.Id),
                Category = retrieved.Category.ToString(),
                ImageFile = retrieved.ImageBaseName
            };
            return conversion;
        }
    }
}
