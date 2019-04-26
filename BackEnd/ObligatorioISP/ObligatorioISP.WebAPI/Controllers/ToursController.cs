using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;

namespace ObligatorioISP.WebAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ToursController : ControllerBase
    {
        private IToursService tours;
        public ToursController(IToursService toursRepo)
        {
            tours = toursRepo;
        }

        [HttpGet]
        public IActionResult Get([FromQuery]double lat, [FromQuery]double lng, [FromQuery]double dist)
        {
            ICollection<TourDto> retrieved = tours.GetToursWithinKmRange(lat, lng, dist);
            return Ok(retrieved);
        }

        [HttpGet("{id}")]
        public IActionResult Get(int id)
        {
            TourDto retrieved = tours.GetTourById(id);
            return Ok(retrieved);
        }
    }
}