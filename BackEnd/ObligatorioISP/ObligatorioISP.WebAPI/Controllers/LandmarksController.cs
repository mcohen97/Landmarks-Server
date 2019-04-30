using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;

namespace ObligatorioISP.WebAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class LandmarksController : ControllerBase
    {
        private ILandmarksService landmarks;
        public LandmarksController(ILandmarksService landmarksRepo) {
            landmarks = landmarksRepo;
        }

        [HttpGet]
        public IActionResult Get([FromQuery]double lat, [FromQuery]double lng, [FromQuery]double dist)
        {
            ICollection<LandmarkSummarizedDto> retrieved = landmarks.GetLandmarksWithinZone(lat, lng, dist);
            return Ok(retrieved);
        }
    }
}
