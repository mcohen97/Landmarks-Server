﻿using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.Services.Contracts.Exceptions;

namespace ObligatorioISP.WebAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ToursController : ControllerBase
    {
        private IToursService tours;
        private ErrorActionResultFactory errorFactory;

        public ToursController(IToursService toursRepo)
        {
            tours = toursRepo;
            errorFactory = new ErrorActionResultFactory(this);
        }

        [HttpGet]
        public IActionResult Get([FromQuery]double lat, [FromQuery]double lng, [FromQuery]double dist)
        {
            IActionResult result;
            try
            {
                ICollection<TourDto> retrieved = tours.GetToursWithinKmRange(lat, lng, dist);
                result = Ok(retrieved);
            }
            catch (ServiceException e) {
                result = errorFactory.GenerateError(e);
            }
            return result;
        }

        [HttpGet("{id}")]
        public IActionResult Get(int id)
        {
            IActionResult result;
            try
            {
                TourDto retrieved = tours.GetTourById(id);
                result= Ok(retrieved);
            }
            catch (ServiceException e) {
                result = errorFactory.GenerateError(e);
            }
            return result;
        }
    }
}