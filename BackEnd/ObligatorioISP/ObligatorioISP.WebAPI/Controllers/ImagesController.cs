using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.Services.Contracts.Exceptions;

namespace ObligatorioISP.WebAPI.Controllers
{
    [Route("api/[controller]")]
    public class ImagesController : Controller
    {
        private string imagesDirectory;

        public ImagesController(IConfiguration configuration) {
            imagesDirectory = configuration["LandmarkImages:Uri"];
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> Get(string id)
        {
            IActionResult result;
            try
            {
                string path = $"{imagesDirectory}/{id}";
                FileStream image = System.IO.File.OpenRead(path);
                result = File(image, "image/jpeg");
            }
            catch (System.IO.IOException) {
                result = NotFound(new ErrorDto() { ErrorMessage = "Image not found" });
            }
            return result;
        }
    }
}
