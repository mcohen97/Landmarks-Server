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
    public class LandmarksController : ControllerBase
    {
        private ILandmarksRepository landmarks;
        public LandmarksController(ILandmarksRepository landmarksRepo) {
            landmarks = landmarksRepo;
        }

        [HttpGet]
        public IActionResult Get([FromQuery]double lat, [FromQuery]double lng, [FromQuery]double dist)
        {
            ICollection<LandmarkDto> retrieved = landmarks.GetWithinZone( lat, lng, dist);
            return Ok(retrieved);
        }
    }
}
