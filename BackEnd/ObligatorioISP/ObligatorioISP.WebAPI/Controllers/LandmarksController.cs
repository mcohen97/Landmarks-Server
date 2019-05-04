using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.Services.Contracts.Exceptions;

namespace ObligatorioISP.WebAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class LandmarksController : ControllerBase
    {
        private ILandmarksService landmarks;
        private ErrorActionResultFactory errorFactory;
        public LandmarksController(ILandmarksService landmarksRepo) {
            landmarks = landmarksRepo;
            errorFactory = new ErrorActionResultFactory(this);
        }

        [HttpGet]
        public IActionResult Get([FromQuery]double lat, [FromQuery]double lng, [FromQuery]double dist)
        {
            ICollection<LandmarkSummarizedDto> retrieved = landmarks.GetLandmarksWithinZone(lat, lng, dist);
            return Ok(retrieved);
        }

        [HttpGet("{id}")]
        public IActionResult Get(int id)
        {
            IActionResult result;
            try
            {
                LandmarkDetailedDto retrieved = landmarks.GetLandmarkById(id);
                result = Ok(retrieved);
            }
            catch (ServiceException e) {
                result=errorFactory.GenerateError(e);
            }
            return result;
        }

        [HttpGet("tour/{id}")]
        public IActionResult GetByTour(int id)
        {
            IActionResult result;
            try
            {
                ICollection<LandmarkSummarizedDto> retrieved = landmarks.GetLandmarksOfTour(id);
                result = Ok(retrieved);
            }
            catch (ServiceException e)
            {
                result = errorFactory.GenerateError(e);
            }
            return result;
        }
    }
}
