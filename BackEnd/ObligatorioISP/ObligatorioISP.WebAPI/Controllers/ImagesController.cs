using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using ObligatorioISP.Services.Contracts.Dtos;

namespace ObligatorioISP.WebAPI.Controllers
{
    [Route("api/[controller]")]
    public class ImagesController : Controller
    {
        private string landmarksImagesDirectory;
        private string toursImagesDirectory;

        public ImagesController(IConfiguration configuration)
        {
            landmarksImagesDirectory = configuration["LandmarkImages:Uri"];
            toursImagesDirectory = configuration["TourImages:Uri"];
        }

        [HttpGet("landmarks/{id}")]
        public async Task<IActionResult> GetLandmarkImage(string id)
        {
            return await GetImage(landmarksImagesDirectory, id);
        }

        [HttpGet("tours/{id}")]
        public async Task<IActionResult> GetTourImage(string id)
        {
            return await GetImage(toursImagesDirectory, id);
        }

        private async Task<IActionResult> GetImage(string imagesDirectory, string id)
        {
            IActionResult result;
            try
            {
                string path = $"{imagesDirectory}/{id}";
                FileStream image = System.IO.File.OpenRead(path);
                result = File(image, "image/jpeg");
            }
            catch (IOException)
            {
                result = NotFound(new ErrorDto() { ErrorMessage = "Image not found" });
            }
            return result;
        }
    }
}
