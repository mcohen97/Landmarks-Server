using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;

namespace ObligatorioISP.WebAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ToursController : ControllerBase
    {
        private IToursRepository tours;
        public ToursController(IToursRepository toursRepo)
        {
            tours = toursRepo;
        }

        [HttpGet]
        public IActionResult Get(double leftBottomLat, double leftBottomLng, double distanceInKm)
        {
            ICollection<TourDto> retrieved = tours.GetToursWithinKmRange(leftBottomLat, leftBottomLng, distanceInKm);
            return Ok(retrieved);
        }

        [HttpGet("{id}")]
        public IActionResult Get(int id)
        {
            TourDto retrieved = tours.GetById(id);
            return Ok(retrieved);
        }
    }
}