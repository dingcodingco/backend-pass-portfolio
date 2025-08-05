# from PIL import Image
# import torch
# from transformers import Blip2Processor, Blip2ForConditionalGeneration

# def generate_caption_from_image(image_path, prompt="이 사진에 어울리는 감성적인 한 줄 광고 문구를 만들어줘"):
#     processor = Blip2Processor.from_pretrained("Salesforce/blip2-opt-2.7b")
#     model = Blip2ForConditionalGeneration.from_pretrained("Salesforce/blip2-opt-2.7b", torch_dtype=torch.float16)
#     device = "cuda" if torch.cuda.is_available() else "cpu"
#     model.to(device)

#     raw_image = Image.open(image_path).convert('RGB')
#     inputs = processor(raw_image, text=prompt, return_tensors="pt").to(device)
#     generated_ids = model.generate(**inputs, max_new_tokens=30)
#     caption = processor.batch_decode(generated_ids, skip_special_tokens=True)[0].strip()
#     return caption


# image.py
from PIL import Image
import torch
from transformers import Blip2Processor, Blip2ForConditionalGeneration

device = "cuda" if torch.cuda.is_available() else "cpu"
processor = Blip2Processor.from_pretrained("Salesforce/blip2-opt-2.7b")
model = Blip2ForConditionalGeneration.from_pretrained(
    "Salesforce/blip2-opt-2.7b", 
    torch_dtype=torch.float16 if torch.cuda.is_available() else torch.float32
).to(device)

# image.py
def generate_caption_from_image(image_path):
    raw_image = Image.open(image_path).convert('RGB')
    inputs = processor(raw_image, return_tensors="pt").to(device)
    generated_ids = model.generate(**inputs, max_new_tokens=30)
    caption = processor.batch_decode(generated_ids, skip_special_tokens=True)[0].strip()
    return caption

